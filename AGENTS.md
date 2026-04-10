# Shane's Planet Blog Writing Guide

This repository stores source drafts for Shane's Planet. Any agent writing a new post in this repository should use this file as the primary writing guide.

## 1. Core Principles

- Write the final post in Korean.
- Do not jump straight into drafting. First understand what kind of post should be written.
- If the user provides draft text, notes, research, logs, screenshots, links, or sample sentences, use those first.
- Treat this document as the primary style reference.
- Existing posts in the repository are secondary references, used only when needed for fact-checking, overlap checks, or internal links.
- Prefer real experience, problem solving, comparison, and decision-making over abstract explanation.
- Do not stay neutral for the sake of neutrality. Make clear judgments and recommendations when the material supports them.
- Be confident without exaggeration. Support claims with logs, code, screenshots, measurements, and official documentation.

## 2. How To Use Existing Posts

This file should already contain enough stylistic guidance on its own. Existing posts are not the main source of style, but they can still be useful in a few situations.

### Main Rules

- Before writing, inspect `git log` broadly and identify the 5 most recent real blog posts.
- Use those 5 posts to calibrate tone, paragraph length, the speed of the `Intro`, the density of `마치며`, and the balance between recommendation and caveat.
- If there is an older post on the same topic, check whether the new post overlaps too much with it.
- If there is an older post on the same topic, consider linking to it from the new post.
- When reading older posts, do not mimic wording mechanically. Only check whether key assumptions, terms, or background explanations were already covered.
- If the user already provided a rich draft and enough background material, you may skip extra exploration.
- Never copy the phrasing of a single existing post too closely.

### Pre-Writing Check

```bash
git log --date=short --pretty=format:'%ad %h %s' --name-only -- '*.md' | head -n 120
```

- Review the log generously, then select 5 actual recent posts.
- Exclude non-post files such as `README.md`, `AGENTS.md`, or project help files.
- Do not stop at the title. Open the body of each selected post.
- Use this step only to tune the final tone, not to outsource judgment.
- The main basis for content should still be the user's materials and the topic being written.

## 3. Information Gathering Before Writing

If the user only gives a topic, do not draft immediately. First ask: "What do I need in order to write this well?"

### When The User Provides Material

- Treat the user's draft, notes, research, logs, commands, screenshots, comparison criteria, links, and sentence samples as first-class source material.
- Do not ask again for information the user already provided.
- If the user gives a draft, preserve its core facts, motivation, unique experience, and natural flow as much as possible while reshaping it into Shane's Planet style.
- Even if the user's text is rough, preserve concrete details and personal judgments when possible.
- If the user provides older writing samples or a preferred tone sample, reflect them as long as they do not conflict with the overall repository style.
- Prefer restructuring, tone cleanup, clarification, enrichment, and deduplication over throwing the entire draft away.
- Ask follow-up questions only after reading everything the user gave you and only if essential information is still missing.

### Information You Should Usually Confirm

- What problem, event, or motivation triggered the post?
- What was the actual environment?
- Are there time-sensitive facts such as versions, prices, limits, or dates?
- What was tried first, and why did it fail?
- What was finally chosen or fixed?
- If this is a comparison post, what were the decision criteria?
- Are there measurable results?
- Are there logs, screenshots, commands, code, or other evidence?

### How To Ask Questions

- Ask for missing substance, not formalities.
- Do not ask too many questions at once.
- Prefer 2 to 4 short, concrete questions.
- If the user already provided enough context, write without asking more.
- If the user provided a lot of draft material, prioritize organizing and rewriting over questioning.

### Example Question Patterns

- Problem-solving post: what broke, in what environment, how the cause was found, and what fixed it
- Comparison post: what was compared, what the criteria were, and what was ultimately chosen
- Review post: what was used, what differed from expectations, and whether it is worth continuing to use
- Tutorial post: when this workflow is useful and how prepared the reader needs to be

## 4. Default Tone

- Use calm declarative prose.
- Use a natural first-person perspective grounded in real usage.
- Mix explanation with judgment.
- Keep paragraphs short and dense.
- Do not over-expand every step. Keep only the steps that matter.
- If needed, still provide a practical guide that gently leads the reader through the work.

### Frequently Used Korean Transition / Judgment Phrases

Use these naturally when they fit the sentence:

- `다만`
- `반면`
- `결국`
- `물론`
- `그래도`
- `특히`
- `실제로`
- `이제`
- `이 경우`
- `개인적으로`
- `확인해보니`
- `생각한다`
- `추천한다`
- `...인 듯하다`
- `...인 셈이다`

### Sentence-Level Style

- Default sentence endings should be calm Korean declaratives such as `~다`, `~했다`, `~된다`, and `~것이다`.
- Establish context from the first paragraph.
- Reveal the direction, result, or conclusion within the first two paragraphs when possible.
- Keep paragraphs compact. In most cases, 1 to 4 sentences per paragraph is enough.
- Avoid exclamations and inflated rhetoric.
- Do not become sterile or robotic.
- Short side comments, restrained impressions, and light humor are acceptable, but only in moderation.
- Optimize for usefulness.
- Help the reader understand "what to do now" as early as possible.

## 5. Title Rules

Titles should be search-friendly. Do not hide the technology, platform, issue, action, or result.

### Preferred Korean Title Patterns

- `X 출시 소식 및 사용 후기`
- `X 설치하기 및 후기`
- `X 문제해결`
- `X 활용해 Y 하기`
- `X 비교 사용기`
- `X가 작동하지 않을 때`
- `[Spring Boot] X 설정`
- `[Java] X 이해하기`
- `[Linux] X 해결`

### Title Principles

- The title should immediately help a reader arriving from search.
- Prefer concrete symptoms, tool names, versions, and outcomes over abstract wording.
- In comparison posts, make the compared items explicit.
- In review posts, do not leave the title as pure opinion. State what was actually used.

## 6. Default Structure

Use this structure unless the topic strongly calls for something else.

```md
# Title

## Intro

[1 to 4 short paragraphs]

## Main Section 1

## Main Section 2

## Main Section 3

## 마치며

**References**

- ...
```

### `Intro` Rules

- Use `## Intro` by default.
- Keep it to 1 to 4 short paragraphs.
- The first paragraph should establish the real situation, trigger, or problem.
- By the second paragraph, reveal the direction, conclusion, or core judgment.
- Include price, rate limit, version, or prior experience when relevant.
- If natural, include a scope-setting sentence such as `이번 글에서는 ...`.
- Start with real experience, friction, a comparison trigger, or a decision motive rather than with a dictionary-style definition.

### Body Rules

- Usually organize the post into 2 to 5 `##` sections.
- Prefer functional section titles over poetic ones.
- Use `###` subsections when the post benefits from smaller steps.
- Favor a flow like `background -> real example -> verification -> caveat`.
- Each section should usually carry one main point.

### Heading Hierarchy Rules

- Treat `#`, `##`, and `###` as different abstraction levels.
- `#` is the single search-friendly post title. It should expose the technology, action, problem, or result.
- `##` is a high-level section heading. Keep it short and functional, usually 1 to 4 words such as `문제`, `해결`, `설치`, `설정`, `테스트`, `사용 후기`, `결론`, `마치며`.
- Do not use long sentence-like `##` headings that already explain the body content. Avoid headings like `왜 SearXNG를 찾게 되었나`, `설치와 현재 테스트 설정`, `지금 단계에서 느낀 장점과 주의점`.
- `###` is where concrete items should go: comparison points, actual steps, config files, causes, cautions, decision criteria, or measured results.
- Keep `###` short too. In recent posts, these are usually concise labels such as `Rate Limit`, `Ollama`, `원인`, `리소스 생성`, `API 연동`, `결과`, `compose.yaml`, `settings.yml`.
- Avoid sentence-like `###` headings when a shorter label will do. Prefer `Brave`, `Playwright`, `버전`, `JSON`, `Naver` over headings such as `Brave Search가 아쉬웠던 이유`, `SearXNG를 찾게 된 이유`, `OpenClaw 연동 전에 볼 것`.
- If one `##` section contains multiple concrete points, split them into `###` subsections instead of making the `##` itself overly specific.
- A short or simple post does not have to use `###`. If the flow is simple, `##` plus compact paragraphs can be enough.
- Keep heading abstraction consistent within the same post: upper levels should stay broad, lower levels should carry the details.
- When possible, prefer section labels that already appear often in this repository over inventing new abstract nouns. Common patterns include `문제`, `해결`, `설치`, `설정`, `테스트`, `사용 후기`, `비교해본 서비스들`, `결론`, `마치며`.

Example:

```md
## 문제
### Brave
### Playwright

## 설정
### compose.yaml
### settings.yml

## 사용 후기
### 장점
### 주의사항
```

#### Default Flow: Problem-Solving Post

- `문제`
- `원인`
- `해결`
- `확인`
- `마치며`

#### Default Flow: Installation / Walkthrough Post

- `설치`
- `설정`
- `테스트` or `실습`
- `주의사항`
- `마치며`

#### Default Flow: Comparison / Review Post

- `비교 기준`
- `항목별 비교`
- `선택 이유`
- `마치며` or `결론`

### Closing Rules

- Use `## 마치며` by default.
- `## 결론` is also acceptable for strongly summary-driven comparison posts.
- Keep the closing short: usually 1 to 3 paragraphs.
- End with a short recap, a recommendation or judgment, and a caveat if needed.

## 7. Code, Commands, Images, Quotes, and Links

### Code and Commands

- Include only reusable real code, commands, or config.
- Always explain code blocks before and/or after them.
- Do not drop code without interpretation.
- Prefer before/after comparisons, minimal reproductions, and real config snippets.
- Wrap tool names, commands, versions, prices, config keys, and model names in backticks when practical.
- Favor practical snippets that readers can reuse immediately.
- More code blocks are not automatically better. Keep only what supports the post's core point.

### Images

- Use images as evidence, not decoration.
- Add them only when UI steps, issue reproduction, or result verification truly matter.
- Select only the scenes that matter.
- Put a short blockquote caption directly below the image.
- Do not document every single screen unless the workflow truly depends on it.

Example:

```md
![1](...)

> 설정이 정상 반영된 상태
```

### Quotes

- Use short quotes for key official wording, warnings, or screenshot explanation.
- Prefer summary plus interpretation over long quotations.
- After quoting, explain why the quote matters.

### Links

- When helpful, link older related posts naturally inside the body.
- Prefer official docs, issue trackers, release notes, and pricing pages as evidence.
- Add `**References**` at the end when appropriate.

## 8. Evidence and Detail

Actively include details such as:

- concrete versions
- prices
- usage limits
- model names
- dates
- CVE numbers
- real logs
- test results
- failure cases

Do not stop at "good" or "bad." Explain why, and support the judgment with evidence or numbers when possible.

If information is time-sensitive, verify it again at the time of writing.

## 9. Current Blog Tendencies

- Posts are shorter and denser than before.
- The `Intro` reaches the point faster.
- Screenshots are used selectively.
- The blog increasingly favors reusable settings, rules, scripts, and workflows over vague impressions.
- Comparison posts should clearly state what is recommended in the end.
- Closings should be brief but should still include both recommendation and caveat when relevant.

## 10. Shane Style Checklist

- Does the post include `## Intro`?
- Is the title search-friendly?
- Does the post begin with a real experience, problem, or trigger?
- Does the direction or conclusion appear early?
- Is the body organized into functional sections?
- Are code and commands reusable and properly explained?
- Does the post include limitations and caveats, not only strengths?
- Are concrete details such as versions, prices, dates, or limits included where needed?
- Does the closing contain a judgment or recommendation?
- If relevant, does the post end with `**References**`?

## 11. What To Avoid

- Do not mechanically copy the long, old tutorial style.
- Do not imitate the wording of one specific older post too closely.
- Do not tell only the success story while hiding failures and caveats.
- Do not sound like marketing copy.
- Do not overuse excitement or hype.
- Do not drag out abstract explanation without concrete application.
- Do not let the post become a pile of code blocks and screenshots with no narrative.
- Do not end vaguely.

## 12. Final Writing Procedure

1. Read the user's draft, notes, research, sentence samples, and links first.
2. If needed, inspect related older posts only to check overlap, internal links, or facts.
3. If essential facts are still missing, ask a few short, concrete questions.
4. Decide the title, `Intro`, and main section structure first.
5. Rewrite while preserving the user's core facts, unique experience, and main judgments.
6. Add practical details such as commands, code, screenshots, and links.
7. Add limitations and caveats.
8. Close with `마치며` and `References` when appropriate.
9. Review the final draft against this document's tone, structure, and density rules.
